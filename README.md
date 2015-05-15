# TangibleXML

TangibleXML is an Android library that automatically parses XML into convenient objects through annotations and reflection. It's really easy to use, through the power of annotations.

TangibleXML was inspired by Alex Gilleran's IceSoap (actually, its introduction docs), which was too advanced for my needs. Not sure how similar it actually ended up being. I know IceSoap supports more advanced selection, based on node attributes (TangibleXML doesn't).

Okay, with the introduction out of the way, let's get started!

## How does it work?

A parser tree is automatically built based on the annotated fields of your classes. Reflection is used to create objects and set fields. You can make fields required or optional, and lists just work without jumping through any hoops.

## How do I use it?

* Annotate your root result class with `@TangibleResult`
* Annotate your fields with `@TangibleField`
* Create a `Parser` object
* Call `parse()` with an `XmlPullParser`

And that's it!

Well, almost. There are a couple of prerequisites on the classes you want to instantiate:

* They must be public.
* They must have a public no-arg constructor.
* The `@TangibleField`s must be public.
* The `@TangibleField`s may not be static or final.
* The `@TangibleField`s may not be initialized (the parser takes null to mean "not found yet", so a non-null start value will make it complain about double values).

### A simple example

    <root>
      <personlist>
        <people>
          <person>
            <name>Snild</name>
            <job>
              <name>MiB</name>
              <salary>12345</salary>
            </job>
          </person>
          <person>
            <name>Bertil</name>
            <thisWillBeIgnored>
              <ExtraStuff><EvenMoreStuff /></ExtraStuff>
            </thisWillBeIgnored>
          </person>
        </people>
      </personlist>
    </root>

    @TangibleResult("/root/personlist")
    public class MyResult {
        @TangibleField("people/person")
        public ArrayList<Person> people;
    }

    public class Job {
        @TangibleField("salary")
        public Integer salary;
        @TangibleField("name")
        public String name;
    }

    public class Person {
        @TangibleField("name")
        public String name;
        @TangibleField(value="job", required=false)
        public Job job;
    }

    public void doSomeParsing(XmlPullParser xml) throws TangibleException {
        Parser<MyResult> parser = new Parser<>(MyResult.class);
        MyResult res = parser.parse(xml);
        ...
    }

You can also take a look at the test classes for a (slightly) more complex example.

## Sweet features

* No object factories required -- everything is done through reflection!
* List fields just work! Each element found at the provided path will be added to the list.
* Pretty okay performance, I think.
* No extras, no fancy stuff. Do your own threading. Make your own HTTP request.

## Limitations/future work

* XML attributes are currently completely ignored. I didn't need them in my own project. Shouldn't be too difficult to add, though.
* There is no fine control over minimum/maximum count for lists. For now, required means "at least one" for lists, and "exactly one" for plain fields.
* It'd be nice to be able to set a "default" for fields, to be used if no match is found.
* You can't use primitive fields (because TangibleXML uses null to check if a required field has been found).
* We use reflection to set up the parser and instantiate objects. That means you don't have to bother with Factory classes, but it also means that `@TangibleField` fields must be the exact concrete class you want. No polymorphism allowed! :)
