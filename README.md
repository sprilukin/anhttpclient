# Anhttpclient library

## anhttpclient library (acronym for "Another HTTP client") intended to reduce amount of efforts to integrate robust and powerful Apache httpclient library into your code

### Example

    WebBrowser webBrowser = new DefaultWebBrowser();
    WebResponse response = webBrowser.getResponse("http://someaddress.com");
    System.out.println(response.getText());

### Using as a maven dependency

    <dependency>
        <groupId>anhttpclient</groupId>
        <artifactId>anhttpclient</artifactId>
        <version>0.3.1</version>
    </dependency>

NOTE: you need to add a maven repository to your pom.xml

    <repositories>
        <repository>
            <id>sprilukin-releases</id>
            <url>https://raw.github.com/sprilukin/mvn-repo/master/releases</url>
        </repository>
    </repositories>

