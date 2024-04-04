# IF3210-2024-Android-DGR



## OWASP ANALYSIS
OWASP analysis is a security analysis that is used to identify the security risks in an application. The analysis is based on the OWASP Top 10, which is a list of the 10 most critical security risks in web applications. The analysis is performed by scanning the application for vulnerabilities and then reporting on the findings. The goal of the analysis is to identify and fix any security risks in the application before they can be exploited by attackers.

in this project we will analyze:
1. Insufficient Input/Output Validation
2. Security Misconfiguration
3. Insecure Data Storage

Let's start the analysis!


### 1. Insufficient Input/Output Validation
Insufficient input/output validation is a security risk that occurs when an application does not properly validate user input or output. This can lead to a variety of security risks, including injection attacks, cross-site scripting, and other vulnerabilities. To mitigate this risk, it is important to validate all user input and output to ensure that it is safe and secure.
in this part i will make an example of insufficient input/output validation with the following part below:

**a. Injections**

we will make an example with fill the email and password with "1'or'1'='1" to show all user that registered in the application.

![input_ouput_insufficient_1.png](assets%2Finput_ouput_insufficient_1.png)

API will response some issue that inform the email and password is invalid and the user can't login to the application. without appearing all user that registered in the application.

![input_ouput_insufficient_2.png](assets%2Finput_ouput_insufficient_2.png)

**b. improper or no input validation**

in this part we will not filling anything in page new transaction and click the button "Add Transaction" to show the error message that inform the user to fill the form.

![input_ouput_insufficient_3.png](assets%2Finput_ouput_insufficient_3.png)

the application will show toast message that inform the user to fill the form and the application still running and not crash.


### 2. Security Misconfiguration

Security misconfiguration is a security risk that occurs when an application is not properly configured to protect against security threats. This can include things like default passwords, unnecessary services, and other configuration issues that can be exploited by attackers. To mitigate this risk, it is important to ensure that the application is properly configured and that all security settings are up to date.

In this part i will make an example of security misconfiguration about make an necessary permission to access the camera and location in the application.

in this application the user should give the permission to access the camera and location to use the feature in the application. if the user not give the permission the application will not run the feature that need the permission.


![missconfiguration_security_1.png](assets%2Fmissconfiguration_security_1.png)  ![missconfiguration_security_2.png](assets%2Fmissconfiguration_security_2.png)

so for what is the feature that need the permission, its because to ensure the user data is safe and secure. the application will not access the camera and location without the user permission.


### 3. Insecure Data Storage

Insecure data storage is a security risk that occurs when an application does not properly store sensitive data. This can include things like storing passwords in plain text, using weak encryption, or not properly securing data at rest. To mitigate this risk, it is important to ensure that all sensitive data is properly encrypted and stored securely.

In this application that have 2 important data that need to be stored securely, it is email and token JWT.
the email and token JWT will be stored in root directory with random name and encrypted with RSA encryption by keystore.
so the token and the email will be stored securely and can't be accessed by other application without key.

![insecure_datastore_1.png](assets%2Finsecure_datastore_1.png)
![insecure_datastore_2.png](assets%2Finsecure_datastore_2.png)

if the user fill something in the email and token file application will denied the access to the file and the application will not crash, but it will go to login page instead.

like this example below:
![insecure_datastore_3.png](assets%2Finsecure_datastore_3.png)
![insecure_datastore_4.png](assets%2Finsecure_datastore_4.png)

and the application will response like this:
![insecure_datastore_5.png](assets%2Finsecure_datastore_5.png)
![insecure_datastore_6.png](assets%2Finsecure_datastore_6.png)