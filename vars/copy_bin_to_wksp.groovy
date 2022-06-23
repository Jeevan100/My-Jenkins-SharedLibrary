Seems Jenkins officially doesn't support upload of binary file yet as you can see in JENKINS-27413. You can still make use of the input step to get binary file in your workspace. We will be using a method to get this working but we will not use it inside the Jenkinsfile otherwise we will encounter errors related to In-process Script Approval. Instead, we will use Global Shared Libraries, which is considered one of Jenkins' best practices.

Please follow these steps:

1) Create a shared library

Create a repository test-shared-library
Create a directory named vars in above repository. Inside vars directory, create a file copy_bin_to_wksp.groovy with the following content:
def inputGetFile(String savedfile = null) {
    def filedata = null
    def filename = null
    // Get file using input step, will put it in build directory
    // the filename will not be included in the upload data, so optionally allow it to be specified

    if (savedfile == null) {
        def inputFile = input message: 'Upload file', parameters: [file(name: 'library_data_upload'), string(name: 'filename', defaultValue: 'demo-backend-1.0-SNAPSHOT.jar')]
        filedata = inputFile['library_data_upload']
        filename = inputFile['filename']
    } else {
        def inputFile = input message: 'Upload file', parameters: [file(name: 'library_data_upload')]
        filedata = inputFile
        filename = savedfile
    }

    // Read contents and write to workspace
    writeFile(file: filename, encoding: 'Base64', text: filedata.read().getBytes().encodeBase64().toString())
    // Remove the file from the master to avoid stuff like secret leakage
    filedata.delete()
    return filename
}
