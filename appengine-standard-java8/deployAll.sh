set +x
gcloud config configuration activate qa
for app in "helloworld" "kotlin-appengine-standard" "kotlin-springboot-appengine-standard" \
      "springboot-appengine-standard" "kotlin-spark-appengine-standard" \
      "sparkjava-appengine-standard"
do
  (cd "${app}"; mvn appengine:deploy -Dapp.deploy.version="${app}" -Dapp.deploy.project="${GOOGLE_CLOUD_PROJECT}")
done

