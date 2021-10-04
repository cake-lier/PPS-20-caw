package it.unibo.pps.caw.common.view.sounds

/** The possible audio types for a given [[Track]]. */
enum AudioType {

  /** The type for looping, long-playing music tracks. */
  case Music

  /** The type for one-shot, short-playing sound tracks. */
  case Sound
}
