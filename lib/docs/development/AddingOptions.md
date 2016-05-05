# Adding Options

in this document the steps for the addition of a new configurable option are
described. Basically two types of options are supported:
+ On-Off-option
+ A multi-value option for which one choice is made.

## Step 1 : Implement it

Of course the implementation of the new option is the first step. You shuld use
a global flag or variable storing the state of the option (on/off or a value
inidcating the chosen option).

## Step 2 : Add it to options.xml

The file lib/js/abbozza/options.xml contains an xml-tree describing the options
shown in the configuraion dialog. The root of the tree is an `<option>`-tag.
It contains a sequence of `<group>`, `<item>` or `<choice>`-tags. 

`<group>`-tags contain subtrees of options. Its only attribute is `name`. Its
value is the name of the localized String used to display it.

New localized strings have to be added to lib/js/languages/<language>.xml.

`<item>`-tags describe an on/off-option. They have two attributes. `name` is the
name of the localized string, while `option` is the name of the property used
by abbozza.

`<choice>`-tags describe a multi-valued-option. Exactly one of the choices in the
group has to be set. The `name`- and `option`-attributes are the same as for
`<item>`-tags

After this step the options are available in the configuration dialog.

## Step 3 : How to access the new options

You can access the value of the new otpions via 
`Configuration.getParameter("option.<name>")`, where `<name>` is the
value of the corresponding `name`-attribute in options.xml. The value
can either be `true` or `false`. I.e. an option with name `fancyOption`
is checked by `Configuration.getParameter("option.fancyOption")`.

If you need something to be done as soon, as the configuration is changed,
you have to add it to `Configuration._apply`.

