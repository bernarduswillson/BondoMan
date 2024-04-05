<!-- LOGO -->
<br />
<div align="center">
  <img src="public/algo.png" alt="Logo" width="80" height="80">

  <h3 align="center">BondoMan</h3>

  <p align="center">
    Tubes 1 of Platform-Based Application Development (IF2211) 
    <br />
    <br />
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
        <a href="#about-the-project">About The Project</a>
        <ul>
            <li><a href="#specification">Specification</a></li>
            <li><a href="#built-with">Built With</a></li>
            <li><a href="#owasp">OWASP</a></li>
        </ul>
    </li>
    <li>
        <a href="#usage">How to Use?</a>
    </li>
    <li>
        <a href="#authors">Author</a>
    </li>
  </ol>
</details>
<br/>
<br/>

<!-- ABOUT THE PROJECT -->
## About The Project

![Bondoman Preview][bondoman-preview]

Tubes 1 of Tubes 1 of Platform-Based Application Development (IF2211). A financial management app to track income, expense, and saving transactions. 
<br/>
<br/>

### Specification

* Navigation
  * Header
  * Bottom navigation bar
* Authentication
  * Login
  * Background service that checks JWT expiration 
  * Logout
* Transaction CRUD
  * View transaction details including its location using Google Maps
  * Add new transaction with automated date and location data
  * Update transaction
  * Delete transaction
* List of transactions
  * Transaction data are stored locally in a Room database
  * View all transactions as transaction list
  * Overall statistics including balance, cashflow, and growth
  * Display list using recycler view for optimization
* Bill scan
  * Add a new transaction entry by capturing a physical bill using a android's camera (Dummy OCR)
* Graphs
  * Statistics are represented by graphic charts
  * The statistics include total income, expenses, and savings within each period of time, displayed using line charts
* Export
  * Transaction data can be exported into a spreadsheet with the format of .xlsx or .xls
* Share
  * Transaction data can be shared through GMail as a spreadsheet file with the format of .xlsx or .xls
* Network sensing
  * Bondoman uses semi-online system that lets user use the app without having to be always connected to the internet 
  * Bondoman can detect current network status and alert the user if internet connection is unavailable
* Broadcast receiver
  * Preferences settings includes randomize transaction to set a random initial data whenever user add a new transaction
* Profile
  * [BONUS] User can customize their profile, including profile picture with optional twibbon overlay
* Security
  * [BONUS] OWASP analysis
<br/>
<br/>

### Built with

#### Tech stack
* ![Kotlin][Kotlin]

#### Libraries
* Jetpack Compose
* KSP
* Dagger Hilt
* Safe Args
* Life Cycle
* Retrofit
* Moshi
* OkHTTP3
* Room
* CameraX
* AndroidMP
* GMS
* POI
<br/>
<br/>

### OWASP Analysis
OWASP analysis is a security analysis that is used to identify the security risks in an application. The analysis is based on the OWASP Top 10, which is a list of the 10 most critical security risks in web applications. The analysis is performed by scanning the application for vulnerabilities and then reporting on the findings. The goal of the analysis is to identify and fix any security risks in the application before they can be exploited by attackers.

in this project we will analyze:
1. Insufficient Input/Output Validation
2. Security Misconfiguration
3. Insecure Data Storage

Let's start the analysis!


#### **1. Insufficient Input/Output Validation**
Insufficient input/output validation is a security risk that occurs when an application does not properly validate user input or output. This can lead to a variety of security risks, including injection attacks, cross-site scripting, and other vulnerabilities. To mitigate this risk, it is important to validate all user input and output to ensure that it is safe and secure.
in this part i will make an example of insufficient input/output validation with the following part below:

**a. Injections**

we will make an example with fill the email and password with "1'or'1'='1" to show all user that registered in the application.

<image src="assets%2Finput_ouput_insufficient_1.png" width="250">

API will response some issue that inform the email and password is invalid and the user can't login to the application. without appearing all user that registered in the application.

<image src="assets%2Finput_ouput_insufficient_2.png" height="250">

**b. improper or no input validation**

in this part we will not filling anything in page new transaction and click the button "Add Transaction" to show the error message that inform the user to fill the form.

<image src="assets%2Finput_ouput_insufficient_3.png" width="250">

the application will show toast message that inform the user to fill the form and the application still running and not crash.
<br />
<br />

#### **2. Security Misconfiguration**

Security misconfiguration is a security risk that occurs when an application is not properly configured to protect against security threats. This can include things like default passwords, unnecessary services, and other configuration issues that can be exploited by attackers. To mitigate this risk, it is important to ensure that the application is properly configured and that all security settings are up to date.

In this part i will make an example of security misconfiguration about make an necessary permission to access the camera and location in the application.

in this application the user should give the permission to access the camera and location to use the feature in the application. if the user not give the permission the application will not run the feature that need the permission.

<image src="assets%2Fmissconfiguration_security_1.png" width="250">
<image src="assets%2Fmissconfiguration_security_2.png" width="250">

so for what is the feature that need the permission, its because to ensure the user data is safe and secure. the application will not access the camera and location without the user permission.
<br/>
<br/>

#### **3. Insecure Data Storage**

Insecure data storage is a security risk that occurs when an application does not properly store sensitive data. This can include things like storing passwords in plain text, using weak encryption, or not properly securing data at rest. To mitigate this risk, it is important to ensure that all sensitive data is properly encrypted and stored securely.

In this application that have 2 important data that need to be stored securely, it is email and token JWT.
the email and token JWT will be stored in root directory with random name and encrypted with RSA encryption by keystore.
so the token and the email will be stored securely and can't be accessed by other application without key.

<image src="assets%2Finsecure_datastore_1.png" height="250">

<image src="assets%2Finsecure_datastore_2.png" height="250">

if the user fill something in the email and token file application will denied the access to the file and the application will not crash, but it will go to login page instead.

like this example below:
<image src="assets%2Finsecure_datastore_3.png" height="250">

<image src="assets%2Finsecure_datastore_4.png" height="250">

and the application will response like this:
<image src="assets%2Finsecure_datastore_5.png" height="250">

<image src="assets%2Finsecure_datastore_6.png" width="250">
<br/>
<br/>

## How To Use
### 1. Login
### 2. View transaction list
### 3. Add new transaction
### 4. View transaction detail
### 5. Edit transaction detail
### 6. Delete transaction
### 7. Scan bill
### 8. View statistics
### 9. Edit Profile
### 10. Export to local
### 11. Share via GMail
### 12. Randomize
### 13. Logout
### 14. No internet alert
<br/>
<br/>

## Author

| NIM | Name | 
| :---: | :--- |
| 10023509 | M. Surya Dharma Khazinatul Azror | 
| 13521019 | Ditra Rizqa Amadia | 
| 13521005 | Bernardus Willson | 
| 13521005 | Fahrian Afdholi | 

<table>
  <tr>
    <th>Task item</th>
    <th>Contributors</th>
  </tr>

  <tr>
    <td rowspan="2">Design</td>  
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Surya Azror</td>
  </tr>

  <tr>
    <td rowspan="1">Splash screen page</td>
    <td>Surya Azror</td>
  </tr>

  <tr>
    <td rowspan="1">Login page</td>
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="1">Navigation components</td>
    <td>Ditra Amadia</td>
  </tr>

  <tr>
    <td rowspan="3">Transaction page</td>
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="1">Transaction details page</td>  
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="1">Scan page</td>  
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="1">Add trasaction page</td>  
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="2">Statistics page</td>  
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="3">Settings page</td>  
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">Profile page</td>  
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="1">Authentication</td>  
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="3">Navigation</td>  
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">Transaction list</td>  
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">Create transaction</td>  
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">View transaction detail</td>  
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">Edit transaction detail</td>  
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">Delete transaction</td>  
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="2">Bill scanning</td>  
    <td>Bernardus Willson</td>
  </tr>
  <tr>
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="1">Statistics</td>  
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="2">Edit profile</td>  
    <td>Ditra Amadia</td>
  </tr>
  <tr>
    <td>Bernardus Willson</td>
  </tr>

  <tr>
    <td rowspan="1">Export data</td>  
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="1">Share data</td>  
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="1">Randomize data</td>  
    <td>Fahrian Afdholi</td>
  </tr>

  <tr>
    <td rowspan="1">End To End Testing</td>  
    <td>Surya Azror</td>
  </tr>

  <tr>
    <td rowspan="1">Documentation</td>  
    <td>Ditra Amadia</td>
  </tr>

  <tr>
    <td rowspan="1">OWASP Analysis</td>  
    <td>Fahrian Afdholi</td>
  </tr>

</table>

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->
[bondoman-preview]: public/application-preview.png
[Kotlin]: https://img.shields.io/badge/kotlin-%237F52FF.svg?style=for-the-badge&logo=kotlin&logoColor=white