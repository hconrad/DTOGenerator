# DTO Generator Plugin
---

## Summary
---
This is a plugin for IntelliJIDEA. The plugin will create a DTO of the 
current Java Class that is active in the editor. 

The plug in can be invoked with `CTRL + ALT + D` or by opening the generator 
pop up `ALT + INSERT`

## Further Information

* Currently the plugin will read all fields of your existing class and just 
copy them to a destination class that is named like your current class with a
 prefix of _DTO_. 
* It will generate getters and setters for all of your fields. 
**NOTE:** If a property extends the Collection class it will create the 
prefix list instead of get for your getter. (Future version may make that an 
option). 
* The generate will also create a convertFromEntity method that will copy all
 values from your entity object into a dto. 
* If a class with the suffix of DTO already exists the plugin will exit and 
not do anything. (Future version will let the user pick a destination)

## Things yet to be implemented
* Field picker that lets the user pick which fields need to go into the DTO
* Popup allowing the user to pick a custom destination for the DTO

