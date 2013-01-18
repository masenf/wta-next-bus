wta-droid Data Model
====================

stop

  * _id
  * stop_id
  * name
  * alias

tag

  * _id
  * tag
  * fk
  * type = (stop, tag)

Tagging is a way to create a nested folder-like hierarchy in a normalized fashion.

Favorites and Recent are special tags, which correspond to certain UI elements, the
remaining tags will be used for storing Favorites folders as well as Landmark stops
(blue, red, plum line, WWU)
