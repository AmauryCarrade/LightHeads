LightHeads
==========

Give them their head!

Command: `/head [headOwner] [receiver]`

#### Permissions

 - `heads.self`: allows an user to receive his own head with `/head`.  
   *Default: everyone.*
 - `heads.others`: allows an user to receive any head using `/head <headOwner>`.  
   *Default: operators.*
 - `heads.give`: allows an user to give a head to another player using `/head <headOwner> <receiver>`.  
   *Default: operators.*
 - `heads.deathDrop`: if a player with this permission die, his head will be dropped, according to the probability set in the config file.  
   *Default: everyone.*

#### Options

 - `pickupSound`: if true, a pickup sound will be played when the head is given and fit in the inventory.
 - `dropOnDeathProbability`: the probability that a head drops when a player with the permission `heads.deathDrop` die.  
   0 = never; 1 = always.

Builds available [on my jenkins server](http://jenkins.carrade.eu/job/LightHeads/).


This plugin is subject to the terms of the Mozilla Public License, v. 2.0.  
If a copy of the MPL was not distributed with this plugin, you can obtain one [here](http://mozilla.org/MPL/2.0/).
