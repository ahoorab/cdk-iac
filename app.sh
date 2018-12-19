#!/bin/bash
exec java -cp target/classes:$(cat .classpath.txt) io.haskins.cdkiac.app.TestApp dev ds