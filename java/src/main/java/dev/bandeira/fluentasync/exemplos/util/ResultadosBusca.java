package dev.bandeira.fluentasync.exemplos.util;

import java.util.List;

public record ResultadosBusca<T> (Integer count,
  String next, String previous, List<T> results) {

}