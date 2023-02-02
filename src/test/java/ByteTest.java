import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;


class People{
    private List<Person> list;

    public People(ArrayList<Person> list) {
        this.list = list;
    }

    public List<Person> getList() {
        return list;
    }

    public void setList(List<Person> list) {
        this.list = list;
    }
}
class Person {
    private int name;

    public Person(int name) {
        this.name = name;
    }

    public int getName() {
        return name;
    }

    public void setName(int name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name=" + name +
                '}';
    }
}
public class ByteTest {
    @Test
    void byteTest() {

        ArrayList<Person> people = new ArrayList<>();

        people.add(new Person(100));
        people.add(new Person(100));
        people.add(new Person(100));
        people.add(new Person(100));
        people.add(new Person(100));

        People peoples = new People(people);

        List<Person> list = peoples.getList();

        list = list.stream()
                .peek(person -> person.setName(200))
                .collect(Collectors.toList());

        peoples.getList().forEach(System.out::println);


    }
}
