#!/bin/bash
java -cp jasypt-1.9.2.jar  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="komli" password=mySecretKey@123 algorithm=PBEWithMD5AndDES
