# Anhttp library

## Anhttp library is intended to reduce amount of efforts to integrate robust and powerful Apache httpclient library into your code

Example

    WebBrowser webBrowser = new DefaultWebBrowser();
    WebResponse response = webBrowser.getResponse("http://code.google.com/p/anhttp");
    System.out.println(response.getText());
    Using as maven dependency
    <dependency>
        <groupId>anhttp</groupId>
        <artifactId>anhttp</artifactId>
        <version>0.2</version>
    </dependency>

## Installing into local maven repository

download last version of anhttp, its sources and pom file and then install them into local maven repository by running next command:

    mvn install:install-file -DgroupId=anhttp \
        -DartifactId=anhttp \
        -Dversion=0.2 \
        -Dfile=anhttp-0.2.jar \
        -Dsources=anhttp-0.2-sources.jar \
        -Dpackaging=jar \
        -DpomFile=anhttp-0.2.pom \
        -DcreateChecksum=true
