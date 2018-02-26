package ballerina.hello;

@Description {value:"Sample annotation configuration"}
@Field {value:"salutation: Salutation value"}
public annotation greeting attach service, function {
    string salutation;
}
