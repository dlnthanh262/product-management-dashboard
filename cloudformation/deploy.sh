#!/bin/bash
set -e  # Dừng khi lỗi
REGION="ap-southeast-1"
STACK_IAM="iam-secret-reader-stack"
STACK_SECRET="jwt-secret-stack"

deploy_stack() {
  local stack_name=$1
  local template_file=$2
  local capabilities=$3

  if aws cloudformation describe-stacks --stack-name $stack_name --region $REGION >/dev/null 2>&1; then
    echo "Updating stack: $stack_name..."
    aws cloudformation update-stack \
      --stack-name $stack_name \
      --template-body file://$template_file \
      --region $REGION \
      $capabilities || echo "No changes for $stack_name."
  else
    echo "Creating stack: $stack_name..."
    aws cloudformation create-stack \
      --stack-name $stack_name \
      --template-body file://$template_file \
      --region $REGION \
      $capabilities
  fi

  echo "Waiting for $stack_name to finish..."
  aws cloudformation wait stack-create-complete --stack-name $stack_name --region $REGION || \
  aws cloudformation wait stack-update-complete --stack-name $stack_name --region $REGION
}

# Deploy tuần tự
deploy_stack $STACK_IAM role.yaml "--capabilities CAPABILITY_NAMED_IAM"
deploy_stack $STACK_SECRET secret.yaml ""

# Outputs
echo "Outputs IAM stack:"
aws cloudformation describe-stacks --stack-name $STACK_IAM --region $REGION \
  --query "Stacks[0].Outputs" --output table

echo "Outputs Secret stack:"
aws cloudformation describe-stacks --stack-name $STACK_SECRET --region $REGION \
  --query "Stacks[0].Outputs" --output table