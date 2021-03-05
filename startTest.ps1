[CmdletBinding()]
Param(
	[Parameter(Mandatory=$true)]
	[String] $athistory_path,
	[Parameter(Mandatory=$false)]
	[String] $report_path = ""
)

Set-Item Env:MAVEN_OPTS "-Dfile.encoding=UTF-8 -Dsurefire.rerunFailingTestsCount=0"
echo ${api_uri}
echo ${sms_uri}

$buildlocation = (get-location).path
$testresult="$buildlocation\target\allure-results"
$hst = "${athistory_path}/history.json"
$hsttrend = "${athistory_path}/history-trend.json"
$outputhst = "$testresult\history\history.json"
$outputhsttrnd = "$testresult\history\history-trend.json"

Set-Location -Path "$buildlocation"
$p = pwd

If (($api_uri -eq "") -or ($api_uri -eq $null)) {
mvn clean test | tee -Variable result
} else {
mvn clean test -Dapi_uri="${api_uri}" -Dsms_uri="${sms_uri}" | tee -Variable result
}

$exitCode = 0
if($result | select-string "BUILD FAILURE"){
$exitCode = 1
}


[Console]::OutputEncoding = [System.Text.Encoding]::GetEncoding("utf-8")
echo " ------------------------------------------------------------------------
 ALLURE BUILD
 ------------------------------------------------------------------------"
New-Item -ItemType directory -Path $testresult\history | Out-Null

try {Invoke-WebRequest -Uri $hst -OutFile $outputhst}
Catch{echo "fail copy history"}
try {Invoke-WebRequest -Uri $hsttrend -OutFile $outputhsttrnd}
Catch{echo "fail copy history trend"}

mvn allure:report
#mvn assembly:single -PzipAllureReport
xcopy $buildlocation\target\site\allure-maven-plugin\* $buildlocation\public /s /i /Y

cd $p
echo ${api_uri}
echo ${sms_uri}
echo -------------------------------------------------------
echo $result | select-string "] Tests run: " | Select-Object Line | Out-String
echo -------------------------------------------------------
echo "Process finished with exit code: $exitCode"
exit $exitCode
