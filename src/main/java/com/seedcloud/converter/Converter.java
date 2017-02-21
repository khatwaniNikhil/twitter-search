package com.seedcloud.converter;

@FunctionalInterface
interface Converter<F, T> {
  T convert(F from);
}
