#!/bin/sh
export J2CPP_HOME=.

java -cp $J2CPP_HOME/lib/*:$J2CPP_HOME/bin de.digitalemil.tocplusplus.Main $*
