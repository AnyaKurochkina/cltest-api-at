import configparser
import re
import subprocess
import os
import signal
import sys
from threading import Thread

from flask import Flask, jsonify

app = Flask(__name__)
config = configparser.ConfigParser()
config.read('config.ini')

project_path = os.path.abspath(config.get('DEFAULT', 'project_path'))
token = config.get('DEFAULT', 'token')

run = False

def clear_console():
    os.system('cls' if os.name == 'nt' else 'clear')
    
def set_response(status, message):
        response = jsonify({'message': message})
        response.headers['Access-Control-Allow-Origin'] = '*'
        response.headers['Access-Control-Allow-Credentials'] = 'true'
        response.status_code = status
        return response

@app.route('/run/<test_plan>/<test_run>/', methods=['GET'])
def run_tests(test_plan, test_run):
    global run

    if run:
        response = set_response(400, 'Tests already running')
        return response

    run = True
    clear_console()

    command = (
        f"mvn clean install -DskipTests exec:java "
        f'-Dexec.mainClass=Pipeline "-Dexec.args=testRunId={test_run} testPlanId={test_plan} '
        f'testItToken={token}"'
    )

    print("Get Tests...")
    result = subprocess.run(
        command, cwd=project_path, capture_output=True, text=True, shell=True
    )

    match = re.search(r".env.testArguments..value=.([^>]*?)'", result.stdout)

    if not match:
        response = set_response(400, 'Bad testPlanId or testRunId')
        run = False
        print(result.stdout)
        return response

    test_thread = Thread(target=run_tests_with_args, args=(match.group(1),))
    test_thread.start()

    response = set_response(201, 'Tests started')
    return response


def run_tests_with_args(test_args):
    global run

    print('Run tests:', test_args)
    command = "mvn clean {0} test".format(test_args)

    with subprocess.Popen(
        command, cwd=project_path, stdout=subprocess.PIPE, stderr=subprocess.STDOUT, bufsize=1, universal_newlines=True,
        shell=True
    ) as result:
        for line in iter(result.stdout.readline, ""):
            print(line, end="")
            if not run:
                result.kill()
                sys.exit(0)
        result.wait()

        return_code = result.returncode

    run = False
    print('Tests completed!', test_args)
    if return_code != 0:
        return False

    return True


def signal_handler(sig, frame):
    global run

    if not run:
        sys.exit(0)

    run = False
    print('Stop signal ...')


if __name__ == "__main__":
    signal.signal(signal.SIGINT, signal_handler)
    app.run(host="localhost", port=1313)
    
    