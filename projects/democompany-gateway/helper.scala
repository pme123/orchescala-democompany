#!/usr/bin/env -S scala shebang
// DO NOT ADJUST. This file is replaced by `./helper.scala update`.

//> using dep democompany::democompany-orchescala-helper:0.1.0-SNAPSHOT

import democompany.orchescala.helper.*


@main
def run(command: String, arguments: String*): Unit =
  CompanyDevHelper.runForGateway(command, arguments*)
