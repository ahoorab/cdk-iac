# cdk-iac - Still in development / idea stage

**20/12/18 - Updated to 0.21**

The idea behind this project is to take the recently announced [CDK](https://awslabs.github.io/aws-cdk/index.html)
and try and build a multiple application management tool around it.

The principal I'm working on a the moment is that where possible there should be very little duplication of code across
stacks belonging to multiple applications. This is where the ability to extend constructs will come in handy, but I'm
not at the stage yet.

## Phase 1 : Stack re-use.
I have a stack at the moment called BeanstalkApIGateway, this creates the following:
* Two Iam Roles
* A Iam Instance profile and attaches to one of the roles
* A Beanstalk Application
* A Beanstalk Environment
* An API Gateway RestAPi
* An API Gateway Resource
* An API Gateway Method

In Resources I have created two folders into which DTAP (and Platform) specific information can be stored. When a stack
is build the appropriate configuration is loaded in when the stack is created, and these parameters are used to customise
the stack for the DTAP.

Currently I have hardcoded a DTAP and platform in the app.sh file, these should ideally be passed in as command line
arguments.

## usage
To perform a CDK action against one of your applications you would do the following 

```./cdk-iac.sh -c synth -t BeanstalkTemplate -a backoffice -d dev -p a```

* -c CDK Command e.g synth, deploy
* -t Name of the template to stamp out
* -a Name to give the application
* -d Your DTAP e.g. dev for your developer account
* -p If you have multiple plaforms in a VPC then use this flag

