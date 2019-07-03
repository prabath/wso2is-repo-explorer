# WSO2 Identity Server Git Repo Explorer


* **Step:0** Clone the git repo with the following command. If you are new to Ballerina, please check this out: https://ballerina.io/. Ballerina version: 0.983.0.

```javascript
:\> git clone https://github.com/prabath/wso2is-repo-explorer.git
```

* **Step:1** Copy rex.sh to a directory where you want to maintain Identity Server git repositories. Alway better to keep this readonly.

* **Step:2** Initialize git repository explorer. This checkout all Identity Server repositories and will take some time.

```javascript
:\> ./rex.sh init
```

* **Step:3** List out all Identity Server related repositories.

```javascript
:\> ./rex.sh list
```

* **Step:4** Find the git repo, by the name of a Jar file (without the version number)

```javascript
:\> ./rex.sh find org.wso2.carbon.identity.authenticator.mutualssl
```
