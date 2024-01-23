import json
import re

word_regex_pattern = re.compile(r"[\w']+")


def generate_routes(file_name):
    with open(file_name, 'r', encoding='utf-8') as f:
        data = json.load(f)

    routes = []
    for path, methods in data['paths'].items():
        print(path)
        for method, values in methods.items():
            if method == 'parameters':
                continue
            operation_id = camel(values['operationId'])
            status = list(values['responses'].keys())[0]
            if 'summary' in values:
                route = f"//{values['summary']}\n"
            else:
                route = ''
            route += f'@Route(method = Method.{method.upper()}, path = "{path}", status = {status})\n'
            route += f"public static Path {operation_id};\n"
            routes.append(route)

    return routes


def camel(string):
    words = re.split('_|-', string)  # Разделяем строку по символам "_" и "-"
    camel_case = words[0]  # Берем первое слово без изменений

    for word in words[1:]:
        camel_case += word.capitalize()  # Капитализируем остальные слова и добавляем их к CamelCase

    return camel_case


if __name__ == '__main__':
    routes = generate_routes('swagger.json')
    with open('routes.txt', 'w') as f:
        f.write('\n'.join(routes))
