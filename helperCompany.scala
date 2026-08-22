#!/usr/bin/env -S scala shebang
// DO NOT ADJUST. This file is replaced by `./helperCompany.scala init`.

//> using dep io.github.pme123::orchescala-helper:0.7.0-SNAPSHOT

import orchescala.helper.dev.DevCompanyHelper

   @main
   def run(command: String, arguments: String*): Unit =
     DevCompanyHelper.run(command, arguments*)
