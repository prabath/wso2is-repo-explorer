# WSO2 Identity Server Git Repo Explorer (rEx)

## Initial Setup

* **Step:0** Clone the git repo with the following command. 

```javascript
\> git clone https://github.com/prabath/wso2is-repo-explorer.git
```

* **Step:1** Copy rex.sh (from *wso2is-repo-explorer* directory) to a directory where you want to maintain Identity Server git repositories. Alway better to keep this readonly.

* **Step:2** Initialize git repository explorer. This will checkout all Identity Server repositories and will take some time.

```javascript
\> ./rex.sh init
```
## Usage 

* **Clone** List out all Identity Server related repositories. If you already performed init, you do not need to do this.

```javascript
\> ./rex.sh clone
```

* **List** List out all Identity Server related repositories. If you already performed init, you do not need to do this.

```javascript
\> ./rex.sh list
```

* **Find** Find the git repo, by the name of a Jar file (without the version number)

```javascript
\> ./rex.sh find org.wso2.carbon.identity.authenticator.mutualssl
```
