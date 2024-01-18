/*
 * Kotlin
 *
 * Copyright 2023-2024 MicroEJ Corp. All rights reserved.
 * Use of this source code is governed by a BSD-style license that can be found with this software.
 */
plugins {
    id("com.microej.gradle.application") version "0.14.0"
}

group = "com.microej.demo"
version = "1.0.0"

microej {
    applicationMainClass = "com.microej.demo.smart_thermostat.Main"
}

dependencies {
    /*
     * Put your project dependencies here. An example of project dependency declaration is provided below:
     *
     * implementation("[org]:[otherArtifact]:[M.m.p]")
     * e.g.: implementation("ej.library.runtime:basictool:1.7.0")
     */
    implementation("ej.api:edc:1.3.5")
    implementation("ej.api:microui:3.4.0")
    implementation("ej.api:drawing:1.0.4")
    implementation("ej.api:microvg:1.4.0")

    implementation("ej.library.ui:mwt:3.5.0")
    implementation("ej.library.ui:widget:5.0.0")
    implementation("ej.library.eclasspath:collections:1.4.2")
    implementation("ej.library.eclasspath:logging:1.2.1")
    implementation("ej.library.eclasspath:stringtokenizer:1.2.0")
    implementation("ej.library.runtime:service:1.2.0")

    /*
     * Put your test dependencies here. An example of test dependency declaration is provided below:
     *
     * testImplementation("[org]:[otherArtifact]:[M.m.p]")
     * e.g.: testImplementation("ej.library.test:junit:1.7.1")
     */

    /*
     * To use a VEE Port published in an artifact repository use this VEE Port dependency.
     */
    microejVeePort("com.nxp.vee.mimxrt1170:evk_platform:2.0.0")
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}