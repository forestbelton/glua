glua [![Build Status](https://travis-ci.org/forestbelton/glua.svg?branch=master)](https://travis-ci.org/forestbelton/glua)
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

Example
-------

Imagine there are two files `src/A.lua` and `src/B.lua` with the following contents:

`src/A.lua`:
```lua
local inc = function(x)
    return x + 1
end

return {
    inc=inc
}
```

`src/B.lua`:
```lua
local A = require('./A')
print(A.inc(0))
```

After running `glua src combined.lua`, `combined.lua` would contain:
```lua
local _MODULES = {}

table.insert(_MODULES, (function()
    local inc = function(x)
        return x + 1
    end
    
    return {
        inc=inc
    }
end)())

table.insert(_MODULES, (function()
    local A = _MODULES[1]
    print(A.inc(0))
end)())
```