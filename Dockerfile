# PLUGIN BUILDER
FROM eclipse-temurin:20-jdk as plugin

COPY src /data/src
COPY pom.xml /data/pom.xml
RUN apt update
RUN apt install maven -y
WORKDIR /data
RUN mvn dependency:resolve
RUN mvn package

# PAPER DOWNLOADER
FROM eclipse-temurin:20-jre AS build
RUN apt-get update -y && apt-get install -y curl jq

ARG version=1.20.4

WORKDIR /opt/minecraft
COPY ./getpaperserver.sh /
RUN chmod +x /getpaperserver.sh
RUN /getpaperserver.sh ${version}

# RUNTIME
FROM eclipse-temurin:20-jre AS runtime

COPY --from=build /opt/minecraft/paperclip.jar /opt/minecraft/paperspigot.jar

VOLUME "/data"

EXPOSE 25565/tcp
EXPOSE 25565/udp

ARG memory_size=32G
ENV MEMORYSIZE=$memory_size

ARG java_flags="-Dlog4j2.formatMsgNoLookups=true -XX:+UseG1GC -XX:+ParallelRefProcEnabled -XX:MaxGCPauseMillis=200 -XX:+UnlockExperimentalVMOptions -XX:+DisableExplicitGC -XX:+AlwaysPreTouch -XX:G1NewSizePercent=30 -XX:G1MaxNewSizePercent=40 -XX:G1HeapRegionSize=8M -XX:G1ReservePercent=20 -XX:G1HeapWastePercent=5 -XX:G1MixedGCCountTarget=4 -XX:InitiatingHeapOccupancyPercent=15 -XX:G1MixedGCLiveThresholdPercent=90 -XX:G1RSetUpdatingPauseTimePercent=5 -XX:SurvivorRatio=32 -XX:+PerfDisableSharedMem -XX:MaxTenuringThreshold=1 -Dusing.aikars.flags=mcflags.emc.gs -Dcom.mojang.eula.agree=true"
ENV JAVAFLAGS=$java_flags

ARG papermc_flags="--nojline"
ENV PAPERMC_FLAGS=$papermc_flags

WORKDIR /data

COPY /docker-entrypoint.sh /opt/minecraft
RUN chmod +x /opt/minecraft/docker-entrypoint.sh

ENTRYPOINT [ "/opt/minecraft/docker-entrypoint.sh" ]
