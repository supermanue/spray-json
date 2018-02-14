import spray.json._


///////////////SIMPLE CLASS
case class TestClass(val id: Int, val info: String, val objects:List[String]){

 override def toString = id.toString + " - " + info.toString +" - " + objects.mkString("/")

}


///////////////SIMPLE CLASS
case class OtherSimpleClass(val id: Int, val info: String, val objects:List[String]){
  override def toString = id.toString + " - " + info.toString +" - " + objects.mkString("/")
}


////////////////POLIMPORPHISM WITH A TRAIT
trait TraitToExport{
  val myParam: Int
  def myMethod(): String
}

case class ClassWithTraitOne(val myParam: Int, val secondParam:String) extends TraitToExport{
  override def toString = "classWithTraitOne:" + myParam.toString + "- " + secondParam.toString
  override def myMethod(): String = "my method in classWithTraitOne"
}


case class ClassWithTraitTwo(val myParam: Int, val secondParam:Int) extends TraitToExport{
  override def toString = "ClassWithTraitTwo:" + myParam.toString + "- " + secondParam.toString
  override def myMethod(): String = "my method in ClassWithTraitTwo"
}


/////////POLIMORPHISM WITH OBJECTS

trait TraitForObject {
  def apply(id: Int): String = "Object"
}

object objectOne extends TraitForObject{
  override def toString: String = "objectOne"
  override def apply(id: Int): String = super.apply(id) + " one ID: " + id.toString
}

object objectTwo extends TraitForObject{
  override def toString: String = "objectTwo"
  override def apply(id: Int): String = super.apply(id) + " two ID: " + id.toString
}




object MyJsonProtocol extends DefaultJsonProtocol {

//////////////////////////////
//////////////////////////////
//////////////////////////////

  implicit object TestClassJsonFormat extends RootJsonFormat[TestClass] {
    def write(t: TestClass) =
      JsArray(JsNumber(t.id), JsString(t.info), t.objects.toJson)

    def read(value: JsValue) = value match {
      case JsArray(Vector(JsNumber(id), JsString(info), objects)) =>
        new TestClass(id.toInt, info, objects.convertTo[List[String]])
      case _ => deserializationError("problem deserializing TestClass")
    }
  }


  //////////////////////////////
  //////////////////////////////
  //////////////////////////////

  // This one makes JSON structure explicit
    implicit object ColorJsonFormat extends RootJsonFormat[OtherSimpleClass] {
      def write(c: OtherSimpleClass) = JsObject(
        "myParam" -> JsNumber(c.id),
        "secondParam" -> JsString(c.info),
        "objects" ->c.objects.toJson
      )
      def read(value: JsValue) = {
        value.asJsObject.getFields("myParam", "secondParam", "objects") match {
          case Seq(JsNumber(id), JsString(info), objects) =>
            new OtherSimpleClass(id.toInt, info, objects.convertTo[List[String]])
          case _ => throw new DeserializationException("problem deserializing OtherSimpleClass ")
        }
      }
    }



//////////////////////////////
/////////////////////////////
//////////////////////////////

  //NOTA: aqui no se exporta la estructura del Json, solo los valores.
  //ocupa menos espacio pero puede ser lioso.

  implicit object ClassWithTraitOneJsonFormat extends RootJsonFormat[ClassWithTraitOne] {
    def write(t: ClassWithTraitOne) =
      JsArray(JsNumber(t.myParam), JsString(t.secondParam))
    def read(value: JsValue) = value match {
      case JsArray(Vector(JsNumber(myParam), JsString(secondParam))) =>
        new ClassWithTraitOne(myParam.toInt, secondParam)
      case _ => deserializationError("problem deserializing ClassWithTraitOne")
    }
  }

  implicit object ClassWithTraitTwoJsonFormat extends RootJsonFormat[ClassWithTraitTwo] {
    def write(t: ClassWithTraitTwo) =
      JsArray(JsNumber(t.myParam), JsNumber(t.secondParam))
    def read(value: JsValue) = value match {
      case JsArray(Vector(JsNumber(myParam), JsNumber(secondParam))) =>
        new ClassWithTraitTwo(myParam.toInt, secondParam.toInt)
      case _ => deserializationError("problem deserializing ClassWithTraitTwo")
    }
  }

  implicit object TraitToExportJsonFormat extends RootJsonFormat[TraitToExport] {
    def write(t: TraitToExport) = t match {
      case one:ClassWithTraitOne =>  JsArray(JsString("classWithTraitOne"), one.toJson)
      case two:ClassWithTraitTwo =>  JsArray(JsString("classWithTraitTwo"), two.toJson)
    }

    def read(value: JsValue) = value match {
      case JsArray(Vector(JsString(classtype), elem)) => classtype match {
        case "classWithTraitOne" => elem.convertTo[ClassWithTraitOne]
        case "classWithTraitTwo" => elem.convertTo[ClassWithTraitTwo]
        case _ => deserializationError("problem deserializing TraitToExport")
      }
      case _ => deserializationError("problem deserializing TraitToExport")

    }
  }


  //////////////////////////////
  /////////////////////////////
  //////////////////////////////

  implicit object TraitForObjectJsonFormat extends RootJsonFormat[TraitForObject] {
    def write(t: TraitForObject) = JsArray(JsString(t.toString))

    def read(value: JsValue) = value match {
      case JsArray(Vector(JsString(objecttype))) => objecttype match {
        case "objectOne" => objectOne
        case "objectTwo" => objectTwo
        case _ => deserializationError("problem deserializing TraitForObject")
      }
      case _ => deserializationError("problem deserializing TraitForObject")

    }
  }
}