#!/bin/bash

# TODO: Have to configure 
# check that owner group exists
if ! getent group atomex &>/dev/null ; then
  groupadd atomex
fi  

# check that user exists
if ! getent passwd atomex &>/dev/null ; then
  useradd --gid atomex atomex
fi

# (optional) check that user belongs to group
if ! id -G -n atomex | grep -qF atomex ; then
  usermod -a -G atomex atomex
fi