# WSO2 Identity Server Repo Explorer (rEx)
WSO2 Identity Server is 100% Open Source!. We maintain  product source code under two GitHub organizations: wso2 and wso2-extensions. These two GitHub organizations carry code related all WSO2 products, with hundreds of repositories. Sometimes it hard to find, which jar file comes from which repo. If you would like to contribute to the product, please check this git repo explorer (rEx).

## Initial Setup
* **Step:1** This relies on Docker, so make sure you have Docker running in your local environment.

* **Step:2** Copy rex.sh (from *wso2is-repo-explorer* directory) to a directory where you want to maintain Identity Server git repositories. Alway better to keep this readonly. Also make the script an executable.
```javascript
\> wget https://github.com/prabath/wso2is-repo-explorer/raw/master/rex.sh
\> chmod +x rex.sh
```

## Usage 

* **Clone** all Identity Server related repositories. This is not a required step to run other commands.

```javascript
\> ./rex.sh clone
```
* **List** out all Identity Server related repositories. You can do this, even without cloning all repos.

```javascript
\> ./rex.sh list
```
* **Update** all Identity Server related repositories. If there are any new repos, those will be cloned. 

```javascript
\> ./rex.sh update
```

* **Find** the git repo(s), by the given name. You can do this, even without cloning all repos.

```javascript
\> ./rex.sh find org.wso2.carbon.identity.authenticator.mutualssl

\> ./rex.sh find OAuth2TokenValidator

\> ./rex.sh find OAuth2TokenValidator.java

\> ./rex.sh find saml-sso
```

* **Update** metadata related to all Identity Server repos. It's better to do an update at least weekly, to find the most up-to-date search results. 

```javascript
\> ./rex.sh update-tree
```

## TODOs

* **Find** the git repo, by the given name and the product version.

```javascript
\> ./rex.sh find 5.8.0/org.wso2.carbon.identity.authenticator.mutualssl
```

* **List** out all Identity Server repos related to the provided product version.

```javascript
\> ./rex.sh list version 5.8.0
```

* **Clone** all Identity Server repos related to the provided product version.

```javascript
\> ./rex.sh clone version 5.8.0
```

* **Find** the git repo(s), by the provided patch number.

```javascript
\> ./rex.sh find patch/PATCH_NUMBER
```

* Spin up the web console

```javascript
\> ./rex.sh serve
```
