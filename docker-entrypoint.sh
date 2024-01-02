#!/bin/sh

exec java -jar -Xms$MEMORYSIZE -Xmx$MEMORYSIZE $JAVAFLAGS /opt/minecraft/paperspigot.jar $PAPERMC_FLAGS nogui
