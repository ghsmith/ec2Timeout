# ec2Timeout
Servlet-based timer for an AWS EC2 instance. Terminates EC2 instance if timer is not reset every 4 hours.

![Image of screenshot](figure01.png)

Requires IAM user with associated policy similar this:

{
    "Version": "2012-10-17",
    "Statement": [
        {
            "Effect": "Allow",
            "Action": [
                "ec2:DescribeInstances",
                "ec2:StartInstances",
                "ec2:StopInstances"
            ],
            "Resource": [
                "*"
            ]
        }
    ]
}
