alter table spell ADD notes varchar(500)
update spell set notes = ''
CREATE TABLE slots (_id INTEGER PRIMARY KEY, id_spell_list int, level_slots int, cantidad_slots int, slots_usados int, id_spells_usados VARCHAR(100))
alter table detalle_spell_list ADD prepared int
update detalle_spell_list set prepared = 1
alter table detalle_spell_list ADD cantidad int
update detalle_spell_list set cantidad = 1
ALTER TABLE spell_list ADD race VARCHAR(15)
ALTER TABLE spell_list ADD abilityMod int
ALTER TABLE spell_list ADD proficiency int
ALTER TABLE spell_list ADD color VARCHAR(10)
UPDATE spell_list SET color = 'default_card'