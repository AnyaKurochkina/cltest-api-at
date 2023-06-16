let requestBody;

function toggleTestIt(testIt) {
    chrome.storage.sync.set({
        'testIt': testIt
    }, function () {
        chrome.action.setIcon({
            path: testIt ? "icons/32-on.png" : "icons/32.png"
        });

        if (testIt) {
            chrome.webRequest.onCompleted.addListener(
                completedListener, {
                urls: ['<all_urls>']
            },
                ['responseHeaders']);
            chrome.webRequest.onBeforeRequest.addListener(
                beforeRequestListener, {
                urls: ['<all_urls>']
            },
                ['requestBody']);
        } else {
            chrome.webRequest.onCompleted.removeListener(completedListener);
            chrome.webRequest.onBeforeRequest.removeListener(beforeRequestListener);
        }
    });
}

function getTestIt(callback) {
    chrome.storage.sync.get('testIt', function (data) {
        var testIt = data.testIt === undefined ? false : data.testIt;
        callback(testIt);
    });
}

function handleIconClick(tab) {
    getTestIt(function (testIt) {
        toggleTestIt(!testIt);
    });
}

async function fetchData(url) {
    try {
        const response = await fetch(url);
        if (response.status === 400) {
            const data = await response.json();
            alert(data.message);
        } else if (response.status !== 201) {
            alert('Unexpected response status');
        }
    } catch (error) {
        alert(error);
    }
}

function isNeedRun(details) {
    return details.method === "POST" && details.url.endsWith("/api/TestRuns");
}

function beforeRequestListener(details) {
    if (isNeedRun(details)) {
        requestBody = details.requestBody;
    }
}

function completedListener(details) {
    if (isNeedRun(details)) {
        chrome.tabs.query({
            active: true,
            currentWindow: true
        }, function (tabs) {
            const decoder = new TextDecoder('utf-8');
            const requestBodyString = decoder.decode(requestBody.raw[0].bytes);
            console.log(requestBodyString);
            const testPlanId = JSON.parse(requestBodyString).testPlanId;
            const id = details.responseHeaders.find(header => header.name === "Location").value.split("/").pop();
            var tabIdVar = tabs[0].id;

            chrome.scripting
            .executeScript({
                target: {
                    tabId: tabIdVar
                },
                args: [`http://localhost:1313/run/${testPlanId}/${id}/`],
                func: fetchData,
            });
        });
    }
}

getTestIt(function (testIt) {
    toggleTestIt(testIt);
});

chrome.action.onClicked.addListener(handleIconClick);
