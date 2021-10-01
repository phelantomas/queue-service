package vandebron

import eu.timepit.refined.W
import eu.timepit.refined.api.{Refined, RefinedTypeOps}
import eu.timepit.refined.string.MatchesRegex

package object domain {
  private type CustomerNumberRegex = MatchesRegex[W.`"^[0-9]{9}$"`.T]

  type CustomerNumber = String Refined CustomerNumberRegex
  object CustomerNumber extends RefinedTypeOps[CustomerNumber, String]
}
