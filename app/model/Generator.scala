package model


object Generator {

  private var tokenCount = 0

  def genNewId = {

    tokenCount = tokenCount + 1

    s"s$tokenCount"
  }

}
