# reflections
SmartThings SmartApp to reflect settings between sets of switches.

**Reflections** was designed to be a part of a solution to a "bedstand lamp and wall switch" problem:
where the lamp on your bedstand is plugged in to an outlet that is controlled by a wall switch.
Since the switch on the lamp is in series with the switch on the wall, both need to be on for the light to come on
but only one needs to be off for the light to go off.
Ideally, the switches would know about each other's state
so that a simple toggle of either one would toggle the light on or off.

This is similar to the "one hallway light with a switch at each end" problem.
Both problems involve two switches and one light.
The hallway problem is typically solved by in-wall wiring between the switches.
This kind of wiring solution is not practical for a bedstand lamp.

Instead, smart switches can be placed behind the conventional ones.
The smart switch in the lamp will control the lamp and will be plugged into an always-on outlet.
The smart switch in the wall will still control the (unused) switched outlet
but will also synchronize its state with the smarts in the lamp.
Both wall and lamp switches will be triggered by their local switches
and work together to toggle the lamp state.

With Z-Wave devices, the smarts may be done with direct group association.
The beauty of this is that the smarts are local and quick.
The ugly part of this is that programming these associations is awkward.
**Reflections** would program them all if it knew how.
In a SmartThings environment the Z-Wave Tweaker was often used to do this kind of thing, one switch at a time.
Once configured, SmartThings plays no part in the mirroring.
Unfortunately, configuration using Z-Wave Tweaker no longer works.

One might think that the SmartThings **Smart Lighting** SmartApp might be used for this.
One could have the smarts in each lamp control the smarts in the wall and
the smarts in the wall control the with 2 automations.
Not only is setting this up awkward, it does not perform well as it still runs in the cloud.
It also does not scale well as there is no way to have multiple lamps and switches work together well.

The SmartThings **Reflections** SmartApp can be used to solve this and more general problems.
For example, consider two bedstand lamps and two wall switches.
The bedstand lamps should be able to be controlled independently of one another at their switch.
The wall switches should be able to control both of them by toggling from the state of the last changed one.

To do this, deploy an instance of the **Reflections** SmartApp with the wall switches in its primary group
and the lamp switches in its secondary group.
Switch and level changes from a device in the primary group (the wall switches) are reflected on all others (both lamps and other wall switches).
Changes from a device in the secondary set (lamps) are reflected only on those in the primary set (wall switches).
