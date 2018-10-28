glua
====

A build tool for Lua source files. Packages all of the `.lua` files within a directory into one, resolving local `require()` calls inline.

Usage
-----

```
Usage: glua [-hV] <directoryName> <outputFile>
resolves calls to require() and combines output into a single lua file
      <directoryName>   The source directory to scan.
      <outputFile>      The output file to generate.
  -h, --help            Show this help message and exit.
  -V, --version         Print version information and exit. 
```
