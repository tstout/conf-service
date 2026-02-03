# conf-service
Minimal Configuration Storage

Currently used for storing credentials. This will likely be expanded to include other types of configuration.

## Example Usage
Create a new entry with user and pass encrypted.
```
clojure -M:conf-service add -e -a '{:name "gmail", :path "gmail-tstout", :user "todd.tstout@gmail.com", :pass "foo-bar"}'
````

Fetch an entry based on the path specified when creating the entry.
```
clojure -M:conf-service fetch --decrypt --path gmail-tstout --url http://stout-pi4:8080/v1/config/

```

## Prerequesites:
This currently has a dependency on [sysloader](https://github.com/tstout/sys-loader) which requires a prepare step:
```bash
 clojure -X:deps prep
```
