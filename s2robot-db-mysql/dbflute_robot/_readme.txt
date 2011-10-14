Directory for DBFlute Client

[First Generating]
DBFlute property is in the directory 'dfprop'.
The core properties are as follows:
 
  o basicInfoMap.dfprop
  o databaseInfoMap.dfprop

You should set up them at first(before first generating)!

After that, You should set up(delete examples and write yours)
SQL file(replace-schema.sql) in the directory 'playsql'
and execute ReplaceSchema task and your tables is created on database.
(If the database have already created by the other way, omit this.)

After that, execute JDBC task and Generate task
and the classes are generated.

The log file(dbflute.log) for tasks is created in the directory 'log'
