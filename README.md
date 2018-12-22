# cdk-iac

**20/12/18 - Updated to 0.21.0**


## overview 
The idea behind this project is to take the recently announced [CDK](https://awslabs.github.io/aws-cdk/index.html)
and try and build a multiple application, management tool, around it.

Over the years I've developed a few systems, (in Python and Java), that essentially did the same thing as CDK does;
programmatically generating CloudFormation templates, and creating the stacks.

I like what they have done so far with CDK, and I'm sure that as it moves closer to version 1.0.0 it will increase in
coverage and functionality. In the mean time ...


## What's this project about?
The idea of this project is to take CDK and build a multi CDK App/Stack / Application management and deployment application.
What I wanted to achieve is to be able to create a CDK Stack that could be reused, along with CDK App that could also be
reused.

I work as a Cloud Engineer / DevOp so I would love to have a single place where I can store and mange all of my AWS 
Resource from. I also want to be able to normalise the stacks by defining Constructs that perform tasks specific to 
my needs without have to duplicate code. 


**Presently with CDK:**
* when you create an 'main' class that you add the Stack too, you hardcode the name of that stack
* you can add parameters to the cdk.json but this isn't really scalable
* there are other ways of passing parameters in, but these could still be annoying to manage

How this project attempts to solve these potential problems:
* the 'main' class (or template as this project refers to them) can be reused across similar applications
* You can define properties in external files to provide good flexibility

# How to use
Using the tool is very simple.
1. git clone the project
2. remove my applications, infrastructure, template and resource files if you will not be needing them
3. add your own applications, infrastructure, template files and create your own resources
4. run the cdk-iac.sh script (see below)


# Implementation
## Structure
### Java classes
* stack : Contains the classes that define the Cloudformation stacks
* template : The 'main' classes that CDK calls initially
* utils : Contains global utility classes


### Resources
* application : Where to store properties files for individual applications
* dtap : Where to store AWS account specific properties
* vpc : Where to store VPC specific properties
Note: DTAP might be renamed in the future.


## usage
To perform a CDK action against one of your applications you would do the following 

```./cdk-iac.sh -c synth -t BeanstalkTemplate -a backoffice -d dev```

* -c CDK Command e.g synth, deploy
* -t Name of the template to stamp out
* -a Name to give the application
* -d Your DTAP e.g. dev for your developer account
* -v If you have multiple vpcs in an AWS then use this flag


### What happens when you use the above command?
1. The .sh script runs the Java class indicated by the -t flag and passes in the properties
2. The 'main' method instaniates the class which calls 'super' through to CdkIacTemplate
3. CdkIacTemplate loads any properties in this order : DTAP then Vpc
4. It then calls up to the original class to get any properties defined there.
5. Properties are stored in this order Dtap, Vpc, Application. Duplicate keys will be overridden by newer properties.
This allows you define an EC2 KeyPair in the DTAP, but override it in the Vpc.
6. Again there is a call to the original class to get the Stack definitions.
7. run is then Invoked on the CDK App
8. CDK then performs whichever command you gave it

### Creating two applications using the same Template but with different configurations
By defining application properties in the resources section you can use the same Template that creates similar stacks, but
with different configurations.

When using the BeanstalkTemplate the following resources will be created:
* IAM Roles
* Beanstalk Application and Environment
* API Gateway RestAPI, Resource and Method

In the /resources/application folder you can see that I have defined two applications:
* microservice
* wordpress

If I run the command ```./cdk-iac.sh -c synth -t BeanstalkTemplate -a wordpress -d dev``` Beanstalk will be configured
with a PHP solution stack and a t2.small instance. Running the command
```./cdk-iac.sh -c synth -t BeanstalkTemplate -a microservice -d dev``` will configured Beanstalk using a Python
Solution Stack and a m5.medium instance.

This could mean, as an example, that you define a consistent infrastructure for your mircoservices (beanstalk behind API
Gateway as an example) then you simple need to define the properties for the individual applications. This makes 'stamping'
out new application infrastructure very easy.


## Created AWS Resource
### Unique ID
Part of my requirements where to enforce a consitent naming convention across all stacks and created resources. There is
a function in the AppProps class that will generate a unique id based on the following data:
* DTAP
* VPC (if provided)
* Application Name

So if you provide all the details the unqiue id would be **dtap-vpc-app_name**, if you miss out VPC then it would be 
**dtap-vpc**. Most of my example stacks use this unqiue ID to name all of their resources. By thinking behind this is 
that each application requires resources, and by ensureing that each associate resource has a unique name that follows
a convention, it is easy to see what belong to what.

As it stands I can see potential problems with multi region, especially for services that don't have the concept of 
different regions e.g IAM so I may need to add a Region flag that adds a region to the unique Id in the future.

### Profiles <- future work
You define a DTAP when you call the cdk-iac.sh file, this is currently used to read the appropriate DTAP properties from 
the resources directory, though the commands still use whatever API keys are configure as your [default] in the 
.aws/credentials file. What I need to do here is use the passed in DTAP parameter and pass this into CDK using the 
--profile parameter in order that the commands run against the correct accounts.

## My Stacks
In the Stacks directory you will find some stacks that I have created by way of seeing if it is possible to migrate our 
existing AWS Resource creation systems (CloudFormation, Ansible, AWS SDK) to use CDK. It is not my intention at this 
time to create Java examples of every possible CfnResource, but it do expect this to fill out as I do the migrations.
