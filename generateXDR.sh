#!/bin/bash
  
# Print the usage message
function printHelp () {
  echo "Usage: "
  echo "  generateXDR.sh <branch_or_commit_hash>"
  echo
  echo "Example of using"
  echo
  echo "    generateXDR.sh master"
  echo "    generateXDR.sh 84135ed642bff4965ce69f9a91f566d6d525188d"
  echo
}

if [ -z "$1" ]; then
    echo "please select branch or commit hash"
    printHelp
    exit 1
fi

language=kotlin
revision=$1
namespace="org.tokend.wallet.xdr"
output_folder="src/main/kotlin/org/tokend/wallet/xdr"

script_path=`dirname "$0"`; script_path=`eval "cd \"$script_path\" && pwd"`
output_folder_full_path="$script_path/$output_folder"

docker pull registry.gitlab.com/tokend/xdrgen-docker
docker run --rm -v $output_folder_full_path:/opt/generated registry.gitlab.com/tokend/xdrgen-docker $language $revision $namespace
