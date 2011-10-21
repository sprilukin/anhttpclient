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

NOTE: you need to add a maven repository to your pom.xml

    <repositories>
        <repository>
            <id>sprilukin-releases</id>
            <url>https://raw.github.com/sprilukin/mvn-repo/master/releases</url>
        </repository>
    </repositories>

