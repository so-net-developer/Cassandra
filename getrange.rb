#!/usr/bin/env ruby
require 'rubygems'
require 'cassandra'
include Cassandra::Constants

class Cassandra1 < Cassandra
  def get_schema
    schema()
  end
end

if ARGV[0].nil? then
  puts "usage: getkey.rb keyspacename columfamily key"
  
  keyspace = Cassandra.new("system")
  keyspace.keyspaces().each { |obj|
    puts obj
  }
  exit
end
if ARGV[1].nil? then
  keyspace = Cassandra1.new(ARGV[0])

  keyspace.get_schema().each { |col,property|
    puts col
  }
  exit
end

if ARGV[2].nil? then
  keyspace = Cassandra.new(ARGV[0])
  keyspace.get_range(ARGV[1]).each { |obj|
    puts obj.key
  }
  exit
end

if ARGV[3].nil? then
  keyspace = Cassandra.new(ARGV[0])
  keyspace.get_range(ARGV[1],:start => ARGV[2]).each { |obj|
    if obj.key == ARGV[2] then
      obj.columns.each { |col|
        puts col.column.name
      }
    end
  }
end

keyspace = Cassandra.new(ARGV[0])
keyspace.get_range(ARGV[1]).each { |obj|
  if obj.key == ARGV[2] then
    obj.columns.each { |col|
      if col.column.name == ARGV[3] then
        puts col.column.value
      end
    }
  end
}



