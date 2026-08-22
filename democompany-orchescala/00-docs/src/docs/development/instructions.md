## Create a Release
General instructions on [Company Documentation](https://pme123.github.io/orchescala/company/development.html#company-documentation)

This is a semi-automatic process. This should be done either to prepare a Release or after a Release.

@:callout(info)
Be aware this requires a Postman Account and a collection, that runs the deployment.

See [Setup Postman](${orchescala.docs}/company/postman.html)
@:@
Do the following steps:

- Check out this project _democompany-orchescala_: `git clone https://YOUR_REPO/democompany-orchescala.git`
- Configure the Release - edit _00-docs/CONFIG.conf_.
- Copy the old Versions from _00-docs/VERSIONS.conf_ to _00-docs/VERSIONS_PREVIOUS.conf_.
- Copy the Versions of the Release to _00-docs/VERSIONS.conf_  from Postman [Manage Deploy: YOUR_ENVIRONMENT](https://YOUR_POSTMAN_URL).
```
    // START VERSIONS

    // Project
    myProjectVersion = "0.8.11" // new
    ...
    // END VERSIONS
    ```
- Prepare Docs Release: `00-docs/helper.scala prepareDocs`

  @:callout(warning)
  Be aware that this overwrites `release.md`
  @:@

- Manually adjust the Release Notes _release.md_.
    - You can check the result, using the _Sbt_ command _laikaPreview_ on [localhost](http://localhost:4242/index.html)
    - If you change the Versions you need to reload _SBT_.
- Publish Docs: `00-docs/helper.scala publishDocs`
- Check the result on [MyCompany Documentation](https://YOUR_DOCUMENTATION)
