package com.example.chapter009;

import org.junit.jupiter.api.Test;

class FileServiceTest {

    @Test
    void example01() {

        FileService.read("file01.txt")
                .subscribe(
                        Util.onNext(),
                        Util.onError(),
                        Util.onComplete()
                );
    }

    @Test
    void example02() {

        FileService.read("file03.txt")
                .subscribe(
                        Util.onNext(),
                        Util.onError(),
                        Util.onComplete()
                );


        FileService.write("file03.txt", "Hello World")
                .subscribe();


        FileService.read("file03.txt")
                .subscribe(
                        Util.onNext(),
                        Util.onError(),
                        Util.onComplete()
                );
    }

    @Test
    void example03() {

        FileService.delete("file04.txt").subscribe();
    }
}
