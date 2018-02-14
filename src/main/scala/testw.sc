import spray.json._
import DefaultJsonProtocol._
import MyJsonProtocol._



println ("hello world")

//DOC EXAMPLE
val source = """{ "some": "JSON source" }"""
val jsonAst = source.parseJson // or JsonParser(source)
val json = jsonAst.prettyPrint // or .compactPrint

val objects = List[String]("rodriguez","pascual")
val jsonList = objects.toJson
val myList = jsonList.convertTo[List[String]]


// SIMPLE CLASS
val mytest = TestClass(1,"manuel", objects)
val jsonTest = mytest.toJson
val newMyTest = jsonTest.convertTo[TestClass]


// SIMPLE CLASS WITH EXPLICIT FORMAT
val mytestSimple = OtherSimpleClass(1,"manuel", objects)
val jsonTestSimple = mytestSimple.toJson
val newMyTestSimple = jsonTestSimple.convertTo[TestClass]


// POLYMORPHISM WITH TRAIT
val classOne = ClassWithTraitOne(1, "one")
val jSonOne = classOne.toJson
val newClassOne = jSonOne.convertTo[ClassWithTraitOne]

val classTwo = ClassWithTraitTwo(2,3)
val jSonTwo = classTwo.toJson
val newClassTwo = jSonTwo.convertTo[ClassWithTraitTwo]


val traitElement: TraitToExport =  classOne
val jSonTrait = traitElement.toJson
val newTraitElement = jSonTrait.convertTo[TraitToExport]

val traitObjects  = List[TraitToExport](classOne, classTwo)
val jSonTraitList = traitObjects.toJson
val newTraitObjects = jSonTraitList.convertTo[List[TraitToExport]]

////////POLYMORPHISM WITH OBJECTS
val i = objectOne(1)
val j = objectTwo(2)

val objectTrait: TraitForObject = objectOne
val objectTraitJson = objectTrait.toJson

val objectList = List[TraitForObject](objectOne, objectTwo)
val objectListJson = objectList.toJson

val newObjectList = objectListJson.convertTo[List[TraitForObject]]
val k = newObjectList(1)
k(1)