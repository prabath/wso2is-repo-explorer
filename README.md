# WSO2 Identity Server Git Repo Explorer (rEx)

## Initial Setup
* **Step:0** This relies on Docker, so make sure you have Docker running in your local environment.

* **Step:1** Copy rex.sh (from *wso2is-repo-explorer* directory) to a directory where you want to maintain Identity Server git repositories. Alway better to keep this readonly. Also make the script an executable.
```javascript
\> wget https://github.com/prabath/wso2is-repo-explorer/raw/master/rex.sh
\> chmod +x rex.sh
```
* **Step:2** Initialize git repository explorer. This will checkout all Identity Server repositories and will take some time.

```javascript
\> ./rex.sh init
```

## Usage 

* **Clone** all Identity Server related repositories. If you already performed init, you do not need to do this.

```javascript
\> ./rex.sh clone
```
* **List** out all Identity Server related repositories. You can do this, even without cloning or init.

```javascript
\> ./rex.sh list
```
* **Update** all Identity Server related repositories. If there are any new repos, those will be cloned.

```javascript
\> ./rex.sh update
```
* **Find** the git repo, by the name of a Jar file (without the version number)

```javascript
\> ./rex.sh find org.wso2.carbon.identity.authenticator.mutualssl
```
## TODOs

* **Clone** all Identity Server repos related to provided version.

```javascript
\> ./rex.sh clone version 5.8.0
```

* **Find** the git repo, by the name of a Jar file name and the product version.

```javascript
\> ./rex.sh find 5.8.0/org.wso2.carbon.identity.authenticator.mutualssl
```
