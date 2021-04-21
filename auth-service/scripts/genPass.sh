#!/bin/bash
java -cp jasypt-1.9.2.jar  org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI input="32sxstM1lvZaz5M" password=mySecretKey@123 algorithm=PBEWithMD5AndDES
