# Anhttp library

## anhttp library (acronym for "Another HTTP") intended to reduce amount of efforts to integrate robust and powerful Apache httpclient library into your code

### Example

    WebBrowser webBrowser = new DefaultWebBrowser();
    WebResponse response = webBrowser.getResponse("http://someaddress.com");
    System.out.println(response.getText());

### Using as a maven dependency

    <dependency>
        <groupId>anhttp</groupId>
        <artifactId>anhttp</artifactId>
        <version>0.3</version>
    </dependency>

### Installing into local maven repository

download last version of anhttp, its sources and pom file and then install them into local maven repository by running next command:

    mvn install:install-file -DgroupId=anhttp \
        -DartifactId=anhttp \
        -Dversion=0.3 \
        -Dfile=anhttp-0.3.jar \
        -Dsources=anhttp-0.3-sources.jar \
        -Dpackaging=jar \
        -DpomFile=anhttp-0.3.pom \
        -DcreateChecksum=true
