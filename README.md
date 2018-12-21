# cdk-iac

**20/12/18 - Updated to 0.21**


## overview 
The idea behind this project is to take the recently announced [CDK](https://awslabs.github.io/aws-cdk/index.html)
and try and build a multiple application management tool around it.

Over the years I've developed a few systems (in Python and Java) that essentially did the same thing as CDK does,
programmatically generating CloudFormation templates, and creating the stacks.

I like what they have done so far with CDK, and I'm sure that as it moves closer to version 1.0.0 it will increase in
coverage and functionality. In the mean time ...


## What's this project about?
The idea of this project is to take CDK and build a multi CDK App/Stack / Application management and deployment application.
What I wanted to achieve is to be able to create a CDK Stack that could be reused, along with CDK App that could also be
reused.


**Presently with CDK:**
* when you create an 'main' class that you add the Stack too, you hardcode the name of that stack
* you can add parameters to the cdk.json but this isn't really scalable
* there are other ways of passing parameters in, but these could still be annoying to manage

How this project attempts to solve these potential problems:
* the 'main' class (or template as this project refers to them) can be reused across similar applications
* You can define properties in external files to provide good flexibility


## Structure
### Java classes
* stack : Contains the classes that define the Cloudformation stacks
* template : The 'main' classes that CDK calls initially
* utils : Contains global utility classes


### Resources
* application : Where to store properties files for individual applications
* dtap : Where to store AWS account specific properties
* platform : Where to store VPC specific properties
Note DTAP might be renamed in the future


## usage
To perform a CDK action against one of your applications you would do the following 

```./cdk-iac.sh -c synth -t BeanstalkTemplate -a backoffice -d dev -p a```

* -c CDK Command e.g synth, deploy
* -t Name of the template to stamp out
* -a Name to give the application
* -d Your DTAP e.g. dev for your developer account
* -v If you have multiple vpcs in an AWS then use this flag


## What happens when you use the above command?
1. The .sh script runs the Java class indicated by the -t flag and passes in the properties
2. The 'main' method instaniates the class which calls 'super' through to CdkIacTemplate
3. CdkIacTemplate loads any properties in this order : DTAP, Platform
4, It then calls up to the original class to get any properties defined there.
5. Properties are stored in this order Dtap, Platform, Application. Duplicate keys will be overridden by newer properties.
This allows you define an EC2 KeyPair in the DTAP, but override it in the Platform.
6. Again there is a call to the original class to get the Stack definitions.
7. run is then Invoked on the CDK App