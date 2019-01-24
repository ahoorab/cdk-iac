# cdk-iac

**24/01/19 - Updated to CDK 0.22.0**

## overview 
The idea behind this project is to take the recently announced AWS [CDK](https://awslabs.github.io/aws-cdk/index.html)
and try and build a multi application management tool around it.

Over the years I've developed a few systems (in Python and Java), that essentially did the same thing as CDK does;
programmatically generating CloudFormation templates, and creating the stacks.

I like what they have done so far with CDK, and I'm sure that as it moves closer to version 1.0.0 it will increase in
coverage and functionality. In the mean time ...

## What's this project about
The idea of this project is to take CDK and build a multi CDK App/Stack / Application management and deployment application.
What I wanted to achieve is to be able to create a CDK Stack that could be reused, along with CDK App that could also be
reused.

I work as a Cloud Engineer so I would love to have a single place where I can store and mange all of my AWS 
Resources from. I also want to be able to normalise the stacks by defining Constructs that perform tasks specific to 
my needs without have to duplicate code across multiple stacks. 

**Presently with CDK:**
*  when you create an 'main' class that you add the Stack too, you hardcode the name of the CloudFormation stack
*  you can add parameters to the cdk.json, but this isn't really scalable across a large number of applications in multiple AWS account and VPCs.
*  there are other ways of passing parameters in, but these could still be annoying to manage

**How this project attempts to solve these potential problems:**
*  the 'main' class (or template as this project refers to them) can be reused across similar applications
*  the name of the CloudFormation stack is dynamic even when using the same Template file
*  uou can define properties in external files to provide good scalability for applications/DTAPs and VPCs.
*  you place everything under Source Control so nothing gets lost, and changes should be reviewed.

# How to use
Using the tool is very simple.
1.  git clone the project
2.  remove my Template, Stacks and resource files if you will not be needing them*
3.  add your own Template and Stack files along with your own resources
4.  run the cdk-iac.sh script (see below)

*If you want to use my Stacks as examples to get yourself started feel free.

# Implementation
## Structure
### Java classes
*  stack : Contains the classes that define the CloudFormation stacks
*  template : The 'main' classes that CDK calls initially, that defines an Application structure
*  utils : Contains global utility classes

### Resources
*  application : Where to store properties files for individual applications
*  [dtap](https://en.wikipedia.org/wiki/Development,_testing,_acceptance_and_production) : Where to store AWS account specific properties
*  vpc : Where to store VPC specific properties
**Note**: DTAP might be renamed in the future.

## usage
The easiest way of using the tool is from the command line with Maven. Whenever you make a Java code change run this 
command ```mvn compile```.

To perform a CDK action against one of your applications you would do the following 

```./cdk-iac.sh [options] synth BeanstalkTemplate backoffice dev```

*  CDK Command e.g synth, deploy, etc
*  Name of the template
*  Name of the application
*  DTAP

**Optional flagz**
*  -v If you have multiple vpcs in an AWS then you can use this flag to target specific a VPC if required

**Cdk Options** : You can also provide the following CDK options when constructing the command.
* --profile (replaces my -p option but still uses the DTAP for the profile name)
* --trace
* --strict
* --ignore-errors
* --json
* --output

### What happens when you use the above command?
1.  The .sh script runs the Java class and passes in the properties
2.  The 'main' method instantiates the class which calls 'super' through to CdkIacTemplate
3.  CdkIacTemplate loads any properties defined in the resources folders
4.  Properties are loaded in this order:- Dtap, Vpc, Application. Duplicate keys will be overridden by newer properties. This allows you define an EC2 KeyPair in the DTAP, but override it with a value in a Vpc.
5.  A call is made to the original class to get the Stack definitions.
6.  run is then Invoked on the CDK App
7.  CDK then performs whichever command you gave it

### Creating two applications using the same Template, but with different configurations
By defining application properties in the resources section you can use the same Template that creates similar stacks, but
with different configurations.

When using the included BeanstalkTemplate for example, the following resources will be created when the BeanstalkApiGateway
stack is called:

*  IAM Roles
*  Beanstalk Application and Environment
*  API Gateway RestAPI, Resource and Method

In the /resources/application folder you can see that I have defined two applications:
*  microservice
*  wordpress

If I run the command   ```./cdk-iac.sh synth BeanstalkTemplate wordpress dev```   Beanstalk will be configured
with a PHP solution stack and a t2.small instance.

Running the command   ```./cdk-iac.sh synth BeanstalkTemplate microservice dev```   will configure Beanstalk 
using a Python Solution Stack and a m5.medium instance.

This means that you can define a consistent infrastructure for your mircoservices (beanstalk behind API
Gateway as an example) then you simply need to define the properties for the individual applications. This makes 
'stamping' out new application infrastructure in a standard and consistent process very easy. And don't forget that by
using the power of CDK you can always update these applications later easily.

## Created AWS Resource
### Unique ID
Part of my requirements was to enforce a consistent naming convention across all stacks and created resources. There is
a method in the AppProps class that will generate a unique id based on the following data:

*  DTAP
*  VPC (if provided)
*  Application Name

If you provide all the above details the unique id would be **dtap-vpc-appname**. If you miss out VPC then it would be 
**dtap-appname**. Most of my example stacks use this unique ID to name all of their resources. My thinking behind this
is that each application requires resources, and by ensuring that each associated resource has a unique name that follows
a convention, it is easy to see what belongs to what.

As it stands I can see potential problems with multi region, especially for services that don't have the concept of 
different regions, e.g IAM. so I may need to add a Region flag that adds a region to the unique Id in the future.
An example of this would be if you wanted to deploy a WordPress application into two different regions, as it stands you
couldn't as the script would try and create IAM resources will the same name twice e.g **dev-wordpress**. By adding the
ability to include the region this might become **dev-eu-west-1-wordpress**, which gets around this issue.

### Profiles
You define a DTAP when you call the **cdk-iac.sh** file, this is currently used to read the appropriate DTAP properties from 
the resources directory, and form part of the unique id. The command uses whatever API keys are configured as
[default] in the .aws/credentials file.

To use a set of credentials that have been defined as a profile in your credentials file then you can use the CDK --profile
flag. I've overridden this functionality so that when this flag is added, the script will attempt to use a profile with
the same name as the passed DTAP. This better fits my needs, and to me makes sense having the profile the same as the DTAP.

# My Stacks
In the Stacks directory you will find some stacks that I have created by way of seeing if it is possible to migrate our 
existing AWS Resource creation systems (CloudFormation, Ansible, AWS SDK) to use CDK. It is not my intention at this 
time to create Java examples of every possible CfnResource, but I do expect this to fill out as I do my migrations, and
test other aspects of the code.
