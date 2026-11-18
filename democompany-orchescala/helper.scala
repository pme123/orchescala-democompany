#!/usr/bin/env -S scala shebang
// DO NOT ADJUST. This file is replaced by `./helperCompany.scala init`.

//> using scala 3.10.1
//> using dep democompany::democompany-orchescala-helper:0.1.0-SNAPSHOT

import democompany.orchescala.helper.*

@main
def run(command: String, args: String*): Unit =
  CompanyOrchescalaDevHelper.runForCompany(command, args*)
