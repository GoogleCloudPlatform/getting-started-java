set -x
set -v
# Temporary directory to store any output to display on error
export ERROR_OUTPUT_DIR
ERROR_OUTPUT_DIR="$(mktemp -d)"
# trap 'rm -r "${ERROR_OUTPUT_DIR}"' EXIT
URL="dot-lesv-qa-999.prom-qa.sandbox.google.com"

export GOOGLE_CLOUD_PROJECT=lesv-qa-999
export CLOUDSDK_API_ENDPOINT_OVERRIDES_APPENGINE='https://staging-appengine.sandbox.googleapis.com/'

# $1 - project
# $2 - PATH
# $3 - search string
function TestIt() {
  curl -X GET "https://${1}-${URL}/${2}" | \
  tee -a "${ERROR_OUTPUT_DIR}/response.txt" | \
  grep "${3}"
}

# gcloud config configurations activate qa

for app in "helloworld" "kotlin-appengine-standard" \
      "kotlin-springboot-appengine-standard" \
      "springboot-appengine-standard" "kotlin-spark-appengine-standard" \
      "sparkjava-appengine-standard"
do
  (cd "${app}"; mvn -B --fail-at-end -q appengine:deploy -Dapp.deploy.version="${app}" \
          -Dapp.deploy.project="${GOOGLE_CLOUD_PROJECT}" -DskipTests=true)
done

echo "******* Test QA Deployed Apps ********"

TestIt "helloworld" "" "Hello App Engine -- Java 8!"
TestIt "helloworld" "hello" "Hello App Engine - Standard using Google App Engine"

TestIt "kotlin-appengine-standard" "" \
  "Hello, World! I am a Servlet 3.1 running on Java8 App Engine Standard, and written in Kotlin..."

TestIt "kotlin-springboot-appengine-standard" "greeting" \
  "Hello, World, from a SpringBoot Application written in Kotlin, running on Google App Engine Java8 Standard..."

TestIt "springboot-appengine-standard" "" \
  "Hello world - springboot-appengine-standard!"

TestIt "kotlin-spark-appengine-standard" "" \
  "Hello Spark Kotlin running on Java8 App Engine Standard."

TestIt "kotlin-spark-appengine-standard" "hello" \
  "Hello Spark Kotlin running on Java8 App Engine Standard."

TestIt "sparkjava-appengine-standard" "" \
  "Hello from SparkJava running on GAE Standard Java8 runtime."
